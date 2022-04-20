package sample

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

			msg: client.filesystem."a.txt".read.contents

			echo: core.#Exec & {
				input: _alpine.output
				args: ["echo", "---\(msg)---"]
				always: true
			}
		}
	}
	client: {
		filesystem: {
			"a.txt": read: contents: string
		}
	}
}