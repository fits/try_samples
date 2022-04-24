package sample1

import (
	"dagger.io/dagger"
	"dagger.io/dagger/core"
)

dagger.#Plan & {
	actions: {
		sample: {
			_alpine: core.#Pull & {
				source: "alpine:3"
			}

			msg: client.filesystem."input.txt".read.contents

			echo: core.#Exec & {
				input: _alpine.output
				args: [
					"sh", "-c",
					"echo -n \(msg)_echo > /tmp/output.txt",
				]
				always: true
			}

			result: core.#ReadFile & {
				input: echo.output
				path: "/tmp/output.txt"
			}
		}
	}
	client: {
		filesystem: {
			"input.txt": read: contents: string
			"output.txt": write: contents: actions.sample.result.contents
		}
	}
}
