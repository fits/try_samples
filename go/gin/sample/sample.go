package main

import "github.com/gin-gonic/gin"

func main() {
    router := gin.Default()

    router.GET("/", func(c *gin.Context) {
        c.String(200, "home")
    })

    router.GET("/users", func(c *gin.Context) {
        c.String(200, "users-all")
    })

    router.GET("/users/:id", func(c *gin.Context) {
        id := c.Param("id")
        c.String(200, "userid-%s", id)
    })

    router.Run()
}