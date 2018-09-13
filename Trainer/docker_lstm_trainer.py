#!/usr/bin/env python3

import os
import pathlib
import sys
import pandas as pd
import numpy as np
import pickle
from scipy import stats
import tensorflow as tf
from sklearn import metrics
from sklearn.model_selection import train_test_split
from tensorflow.python.tools import freeze_graph

# # Customisations

# Input data set
if len(sys.argv) == 1:
    dataset_file = 'wear_dataset.txt'

else:
    dataset_file = str(sys.argv[1])

print("Loaded Dataset: {}".format(dataset_file))


# # Checking prerequisites

if not (os.path.exists(dataset_file)):
    print("Dataset file: '"+dataset_file+"' does not exists!")
    sys.exit()

pathlib.Path('checkpoint').mkdir(parents=True, exist_ok=True)

RANDOM_SEED = 42
columns = ['user', 'activity', 'timestamp', 'x-axis', 'y-axis', 'z-axis']

df = pd.read_csv(dataset_file, header = None, names = columns)
df = df.dropna()

df.head()
df.info()


# # Data preprocessing

N_TIME_STEPS = 200
N_FEATURES = 3
step = 20
segments = []
labels = []

for i in range(0, len(df) - N_TIME_STEPS, step):
    xs = df['x-axis'].values[i: i + N_TIME_STEPS]
    ys = df['y-axis'].values[i: i + N_TIME_STEPS]
    zs = df['z-axis'].values[i: i + N_TIME_STEPS]
    label = stats.mode(df['activity'][i: i + N_TIME_STEPS])[0][0]
    segments.append([xs, ys, zs])
    labels.append(label)

print('Segment Shape: {}'.format(np.array(segments).shape))

reshaped_segments = np.asarray(segments, dtype= np.float32).reshape(-1, N_TIME_STEPS, N_FEATURES)

labels = np.asarray(pd.get_dummies(labels), dtype = np.float32)

print('Reshaped Segment Shape: {}'.format(reshaped_segments.shape))

X_train, X_test, y_train, y_test = train_test_split(
        reshaped_segments, labels, test_size=0.2, random_state=RANDOM_SEED)


# # Building the model

N_CLASSES = 4
N_HIDDEN_UNITS = 64

def create_LSTM_model(inputs):
    W = {
        'hidden': tf.Variable(tf.random_normal([N_FEATURES, N_HIDDEN_UNITS])),
        'output': tf.Variable(tf.random_normal([N_HIDDEN_UNITS, N_CLASSES]))
    }
    biases = {
        'hidden': tf.Variable(tf.random_normal([N_HIDDEN_UNITS], mean=1.0)),
        'output': tf.Variable(tf.random_normal([N_CLASSES]))
    }

    X = tf.transpose(inputs, [1, 0, 2])
    X = tf.reshape(X, [-1, N_FEATURES])
    hidden = tf.nn.relu(tf.matmul(X, W['hidden']) + biases['hidden'])
    hidden = tf.split(hidden, N_TIME_STEPS, 0)

    # Stack 2 LSTM layers
    lstm_layers = [tf.contrib.rnn.BasicLSTMCell(N_HIDDEN_UNITS, forget_bias=1.0) for _ in range(2)]
    lstm_layers = tf.contrib.rnn.MultiRNNCell(lstm_layers)

    outputs, _ = tf.contrib.rnn.static_rnn(lstm_layers, hidden, dtype=tf.float32)

    # Get output for the last time step
    lstm_last_output = outputs[-1]

    return tf.matmul(lstm_last_output, W['output']) + biases['output']

tf.reset_default_graph()

X = tf.placeholder(tf.float32, [None, N_TIME_STEPS, N_FEATURES], name="input")
Y = tf.placeholder(tf.float32, [None, N_CLASSES])

pred_Y = create_LSTM_model(X)

pred_softmax = tf.nn.softmax(pred_Y, name="y_")

L2_LOSS = 0.0015

l2 = L2_LOSS *     sum(tf.nn.l2_loss(tf_var) for tf_var in tf.trainable_variables())

loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits = pred_Y, labels = Y)) + l2

LEARNING_RATE = 0.0025

optimizer = tf.train.AdamOptimizer(learning_rate=LEARNING_RATE).minimize(loss)

correct_pred = tf.equal(tf.argmax(pred_softmax, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, dtype=tf.float32))


# # Training

N_EPOCHS = 50
BATCH_SIZE = 128

saver = tf.train.Saver()

history = dict(train_loss=[],
                     train_acc=[],
                     test_loss=[],
                     test_acc=[])

sess=tf.InteractiveSession()
sess.run(tf.global_variables_initializer())

train_count = len(X_train)

for i in range(1, N_EPOCHS + 1):
    for start, end in zip(range(0, train_count, BATCH_SIZE),
                          range(BATCH_SIZE, train_count + 1,BATCH_SIZE)):
        sess.run(optimizer, feed_dict={X: X_train[start:end],
                                       Y: y_train[start:end]})

    _, acc_train, loss_train = sess.run([pred_softmax, accuracy, loss], feed_dict={
                                            X: X_train, Y: y_train})

    _, acc_test, loss_test = sess.run([pred_softmax, accuracy, loss], feed_dict={
                                            X: X_test, Y: y_test})

    history['train_loss'].append(loss_train)
    history['train_acc'].append(acc_train)
    history['test_loss'].append(loss_test)
    history['test_acc'].append(acc_test)

    if i != 1 and i % 10 != 0:
        continue

    print("epoch: {} test accuracy: {} loss: {}".format(i, acc_test, loss_test))

predictions, acc_final, loss_final = sess.run([pred_softmax, accuracy, loss], feed_dict={X: X_test, Y: y_test})

print()
print("final results: accuracy: {} loss: {}".format(acc_final, loss_final))


# Saving Model:

pickle.dump(predictions, open("lstm_predictions.p", "wb"))
pickle.dump(history, open("lstm_history.p", "wb"))
saver.save(sess, save_path = "./checkpoint/lstm_har.ckpt")
tf.train.write_graph(sess.graph_def, '.', './checkpoint/lstm_har.pbtxt')
sess.close()


# Loading back Model:

history = pickle.load(open("lstm_history.p", "rb"))
predictions = pickle.load(open("lstm_predictions.p", "rb"))


# # Evaluation

# plt.figure(figsize=(12, 8))

# plt.plot(np.array(history['train_loss']), "r--", label="Train loss")
# plt.plot(np.array(history['train_acc']), "g--", label="Train accuracy")

# plt.plot(np.array(history['test_loss']), "r-", label="Test loss")
# plt.plot(np.array(history['test_acc']), "g-", label="Test accuracy")

# plt.title("Training session's progress over iterations")
# plt.legend(loc='upper right', shadow=True)
# plt.ylabel('Training Progress (Loss or Accuracy values)')
# plt.xlabel('Training Epoch')
# plt.ylim(0)

# plt.show()

LABELS = ['Eating&Drinking', 'Walking', 'Running', 'Jumping Jack']

max_test = np.argmax(y_test, axis=1)
max_predictions = np.argmax(predictions, axis=1)
confusion_matrix = metrics.confusion_matrix(max_test, max_predictions)

# plt.figure(figsize=(16, 14))
# sns.heatmap(confusion_matrix, xticklabels=LABELS, yticklabels=LABELS, annot=True, fmt="d")
# plt.title("Confusion matrix")
# plt.ylabel('True label')
# plt.xlabel('Predicted label')
# plt.show()


# # Exporting the model

MODEL_NAME = 'lstm_har'

input_graph_path = 'checkpoint/' + MODEL_NAME+'.pbtxt'
checkpoint_path = './checkpoint/' +MODEL_NAME+'.ckpt'
restore_op_name = "save/restore_all"
filename_tensor_name = "save/Const:0"
output_frozen_graph_name = MODEL_NAME+'.pb'
print('Beginning to freeze graph')
freeze_graph.freeze_graph(input_graph_path, input_saver="",
                          input_binary=False, input_checkpoint=checkpoint_path,
                          output_node_names="y_", restore_op_name="save/restore_all",
                          filename_tensor_name="save/Const:0",
                          output_graph=output_frozen_graph_name, clear_devices=True, initializer_nodes="")
print('Successfully froze graph')
