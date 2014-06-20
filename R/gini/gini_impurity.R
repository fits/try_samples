
gini1 <- function(xs) {
  1 - Reduce("+", lapply(table(xs), function(x) (x / length(xs)) ^ 2))
}

list <- c("A", "B", "B", "C", "B", "A")

gini1(list)
