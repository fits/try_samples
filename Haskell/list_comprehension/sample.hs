
sampleList xs = [x * y | x <- xs, y <- [1..5], odd $ x * y]
