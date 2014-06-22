
# (a) 1 - (AA + BB + CC)
giniA <- function(xs) {
  1 - Reduce("+", lapply(table(xs), function(x) (x / length(xs)) ^ 2))
}

# (b) AB * 2 + AC * 2 + BC * 2
giniB <- function(xs) {
  sum(apply(combn(table(xs), 2), 2, function(x) (x[1] / length(xs)) * (x[2] / length(xs)) * 2))
}

list <- c("A", "B", "B", "C", "B", "A")

giniA(list)
giniB(list)
