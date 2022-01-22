import sys

print(sys.argv)

p = sys.argv[1] if len(sys.argv) > 1 else 'default'

print(p)
