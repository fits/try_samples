terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.67.0"
    }
  }
}

provider "aws" {}

data "aws_caller_identity" "user" {}

output "current_user" {
  value = {
    user_id = data.aws_caller_identity.user.user_id
    arn     = data.aws_caller_identity.user.arn
  }
}