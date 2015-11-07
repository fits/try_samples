
import sys
from sklearn import datasets
from sklearn import svm
from sklearn import cross_validation

k = int(sys.argv[1])

iris = datasets.load_iris()

svc = svm.SVC()

res = cross_validation.cross_val_score(svc, iris.data, iris.target, cv = k)

print(res)
print("accuracy: ", res.mean())
