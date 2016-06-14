
import sys
from model_helper import load_model, save_weights, train_mnist

epoch = 5
mini_batch = 200

model_file = sys.argv[1]
weights_file = sys.argv[2]
weights_dest_file = sys.argv[3]

model = load_model(model_file, weights_file)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', metrics = ['accuracy'])

(x_train, y_train) = train_mnist()

model.fit(x_train, y_train, nb_epoch = epoch, batch_size = mini_batch)

save_weights(model.get_weights(), weights_dest_file)
