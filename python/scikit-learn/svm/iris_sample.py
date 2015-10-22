from sklearn import datasets
from sklearn import svm

iris = datasets.load_iris()

svc = svm.SVC()
res = svc.fit(iris.data, iris.target)

print(res)
