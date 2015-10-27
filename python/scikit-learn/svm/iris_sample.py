from sklearn import datasets
from sklearn import svm

iris = datasets.load_iris()

svc = svm.SVC()
svc.fit(iris.data, iris.target)

print(svc.predict([[5, 3, 4, 1], [6, 3, 5, 2]]))
