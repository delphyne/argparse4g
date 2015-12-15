package com.github.delphyne.argparse4g
import groovy.util.logging.Slf4j
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParser

@Slf4j
class Argparse4g {

	private final ArgumentParser parser

	Argparse4g(String prog, boolean addHelp = true, String prefixChars = ArgumentParsers.DEFAULT_PREFIX_CHARS, String fromFilePrefix = null) {
		parser = ArgumentParsers.newArgumentParser(prog, addHelp, prefixChars, fromFilePrefix)
	}

	def methodMissing(String name, def args) {
		log.debug 'methodMissing(name: {}, args({}): {})', name, args.getClass(), args
		try {
			parser.invokeMethod(name, args)
		} catch (MissingMethodException ex) {
			if (args instanceof Object[]) {
				def argNames = [name]
				for (arg in args) {
					if (arg instanceof String) {
						argNames << arg
					} else {
						break
					}
				}
				def argument = parser.addArgument(argNames as String[])
				if (args[-1] instanceof Closure) {
					args[-1].delegate = argument
					args[-1].resolveStrategy = Closure.DELEGATE_FIRST
					args[-1]()
				}
			}
		}
	}

	def propertyMissing(String name) {
		log.debug 'propertyMissing(name: {})', name
	}

	def propertyMissing(String name, def arg) {
		log.debug 'propertyMissing(name: {}, arg({}): arg', name, arg.getClass(), arg
	}

	ArgumentParser build(Closure config) {
		config.delegate = this
		config.resolveStrategy = Closure.DELEGATE_FIRST
		config.call()
		parser
	}
}
