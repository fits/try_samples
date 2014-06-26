import urllib

str = ';/?:@=&% $-_.+!*\'"(),{}|\\^~[]'

# %3B/%3F%3A%40%3D%26%25%20%24-_.%2B%21%2A%27%22%28%29%2C%7B%7D%7C%5C%5E%7E%5B%5D
print urllib.quote(str)
