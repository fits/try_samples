
get("/") {
	"sample page"
}

get("/:name/:value") {
	"sample page name: ${urlparams.name}, value: ${urlparams.value}"
}
