#!/bin/sh

git whatchanged --oneline | sed '/^[^:]/d' | cut -f 2-3 | sort | uniq -c | sort
