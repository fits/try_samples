importClass java.util.Arrays
importPackage Packages.rx

Observable.from(Arrays.asList [1..10]).filter( (a) -> a % 2 is 0 ).skip(1).take(2).subscribe (a) -> println a
