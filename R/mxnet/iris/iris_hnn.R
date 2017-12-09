library(mxnet)

train_size = 0.7

n = nrow(iris)
perm = sample(n, size = round(n * train_size))

train <- iris[perm, ]
test <-iris[-perm, ]

train.x <- data.matrix(train[1:4])
# 0-2
train.y <- as.numeric(train$Species) - 1

test.x <- data.matrix(test[1:4])
# 1-3
test.y <- as.numeric(test$Species)

mx.set.seed(0)

model <- mx.mlp(train.x, train.y, 
                hidden_node = 5, 
                out_node = 3,
                num.round = 100,
                learning.rate = 0.1,
                array.batch.size = 10,
                activation = 'relu',
                array.layout = 'rowmajor',
                eval.metric = mx.metric.accuracy)

pred <- predict(model, test.x, array.layout = 'rowmajor')

# 1-3
pred.y <- max.col(t(pred))

acc <- sum(pred.y == test.y) / length(pred.y)

print(acc)
