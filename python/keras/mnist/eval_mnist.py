
import sys

from model_helper import load_model, test_mnist

model_file = sys.argv[1]
weights_file = sys.argv[2]

model = load_model(model_file, weights_file)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', metrics = ['accuracy'])

(x_test, y_test) = test_mnist()

(loss, acc) = model.evaluate(x_test, y_test, verbose = 0)

print("loss = %f, accuracy = %f" % (loss, acc))
