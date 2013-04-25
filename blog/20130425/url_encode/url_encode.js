var str = ';/?:@=&% $-_.+!*\'"(),{}|\\^~[]';

// %3B/%3F%3A@%3D%26%25%20%24-_.+%21*%27%22%28%29%2C%7B%7D%7C%5C%5E%7E%5B%5D
console.log(escape(str));

// ;/?:@=&%25%20$-_.+!*'%22(),%7B%7D%7C%5C%5E~%5B%5D
console.log(encodeURI(str));

// %3B%2F%3F%3A%40%3D%26%25%20%24-_.%2B!*'%22()%2C%7B%7D%7C%5C%5E~%5B%5D
console.log(encodeURIComponent(str));
