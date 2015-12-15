package com.github.delphyne.argparse4g
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import org.testng.Assert
import org.testng.annotations.Test

@Test
class Argparse4gTest {

	void testChecksumSample() {
		ArgumentParser p = new Argparse4g('Checksum').build {
			defaultHelp true
			description 'Calculate checksum of given files.'
			'-t' ('--type') {
				choices 'SHA-256', 'SHA-512', 'SHA1'
				setDefault 'SHA-256'
				help 'Specify hash function to use'
			}
			'file' {
				nargs '+'
				help 'File to calculate checksum'
			}
		}

		try {
			p.parseArgs('-h')
			Assert.fail()
		} catch (ArgumentParserException ex) {
			p.handleError(ex)
			assert ex.message == 'Help Screen'
		}

		try {
			p.parseArgs()
		} catch (ArgumentParserException ex) {
			p.handleError(ex)
			assert ex.message == 'too few arguments'
		}

		try {
			p.parseArgs('-t', 'md5')
		} catch (ArgumentParserException ex) {
			p.handleError(ex)
			assert ex.message =~ 'invalid choice'
		}

		def namespace = p.parseArgs('foo')
		assert namespace.get('file') == ['foo']
		assert namespace.get('type') == 'SHA-256'

		namespace = p.parseArgs('-t', 'SHA1', 'bar', 'baz')
		assert namespace.get('type') == 'SHA1'
		assert namespace.get('file') == ['bar', 'baz']
	}
}
