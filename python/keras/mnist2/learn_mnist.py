
import sys

from keras.models import save_model, load_model
from mnist_helper import train_mnist

epoch = 3
mini_batch = 200

model_file = sys.argv[1]
model_dest_file = sys.argv[2]

model = load_model(model_file)

(x_train, y_train) = train_mnist()

model.fit(x_train, y_train, nb_epoch = epoch, batch_size = mini_batch)

save_model(model, model_dest_file)
