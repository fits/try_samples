
gini1 <- function(xs) {
  1 - Reduce("+", lapply(table(xs), function(x) (x / length(xs)) ^ 2))
}

gini2 <- function(xs) {
  sum(apply(combn(table(xs), 2), 2, function(x) (x[1] / length(xs)) * (x[2] / length(xs)) * 2))
}

list <- c("A", "B", "B", "C", "B", "A")

gini1(list)
gini2(list)