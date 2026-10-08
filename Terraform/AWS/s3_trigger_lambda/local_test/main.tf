
provider "aws" {
  skip_credentials_validation = true
  skip_requesting_account_id  = true
}

data "archive_file" "fnc1" {
  type = "zip"
  source_file = "${path.module}/lambda/index.js"
  output_path = "${path.module}/lambda/app.zip"
}

data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"
    principals {
      type = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "fnc1" {
  name = "lambda_exec_role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

resource "aws_lambda_function" "fnc1" {
  filename = data.archive_file.fnc1.output_path
  function_name = "fnc1_function"
  role = aws_iam_role.fnc1.arn
  handler = "index.handler"
  code_sha256 = data.archive_file.fnc1.output_base64sha256
  runtime = "nodejs26.x"
}

resource "aws_lambda_permission" "a1_notify" {
  statement_id = "AllowExecutionFromS3Bucket"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.fnc1.arn
  principal = "s3.amazonaws.com"
  source_arn = aws_s3_bucket.a1.arn
}

resource "aws_s3_bucket" "a1" {
  bucket = "a1-bucket"
}

resource "aws_s3_bucket_notification" "a1_notify" {
  bucket = aws_s3_bucket.a1.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.fnc1.arn
    events = ["s3:ObjectCreated:*"]
  }

  depends_on = [aws_lambda_permission.a1_notify]
}
