package sample2

import (
	"dagger.io/dagger"
	"dagger.io/dagger/core"
)

dagger.#Plan & {
	actions: {
		build: {
			_node: core.#Pull & {
				source: "node:18-alpine"
			}

			src: core.#Copy & {
				input: _node.output
				contents: client.filesystem.".".read.contents
				dest: "/app"
			}

			deps: core.#Exec & {
				input: src.output
				workdir: "/app"
				args: ["npm", "install"]
				always: true
			}

			runBuild: core.#Exec & {
				input: deps.output
				workdir: "/app"
				args: ["npm", "run", "build"]
				always: true
			}

			result: core.#Subdir & {
				input: runBuild.output
				path: "/app/build"
			}
		}
	}
	client: {
		filesystem: {
			".": read: {
				contents: dagger.#FS
				include: ["package.json", "src"]
			}
			"_build": write: contents: actions.build.result.output
		}
	}
}
