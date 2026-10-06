provider "docker" {
  host = var.docker_host
}

resource "docker_image" "alpine" {
  name = "alpine"
}

resource "docker_container" "test1" {
  name  = "test1"
  image = docker_image.alpine.image_id
}

