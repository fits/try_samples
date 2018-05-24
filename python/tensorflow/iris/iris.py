
import pandas as pd
import tensorflow as tf

epoch = 200

df = pd.read_csv('iris.csv')

tf.set_random_seed(0)

w = tf.Variable(tf.zeros([4, 3]))
b = tf.Variable(tf.zeros([3]))

x = tf.placeholder(tf.float32, shape = [None, 4])
y = tf.placeholder(tf.float32, shape = [None, 3])

r = tf.nn.softmax(tf.matmul(x, w) + b)

cross_entropy = tf.reduce_mean(
  tf.nn.softmax_cross_entropy_with_logits(labels = y, logits = r)
)

train_step = tf.train.GradientDescentOptimizer(0.1).minimize(cross_entropy)

init = tf.global_variables_initializer()

ses = tf.Session()

ses.run(init)

species = df['species'].unique().tolist()

labels = tf.one_hot(df['species'].map(species.index).values, len(species))

X = df.iloc[:, :4].values
Y = ses.run(labels)

for ep in range(epoch):
  ses.run(train_step, feed_dict = {x: X, y: Y})

correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(r, 1))

accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

acc = ses.run(accuracy, feed_dict = {x: X, y: Y})

print(f'accuracy : {acc}')
