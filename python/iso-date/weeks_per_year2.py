
def pyear(year):
    return (year + int(year / 4) - int(year / 100) + int(year / 400)) % 7

def isoweek(year):
    return 52 + (1 if pyear(year) == 4 or pyear(year - 1) == 3 else 0)

for y in range(2000, 2030):
    print(f"{y} : {isoweek(y)}")
