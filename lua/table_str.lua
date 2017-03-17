
d = {
  name = "a",
  value = 10
}

print(d)
print("d = " .. tostring(d))

for k, v in pairs(d) do
  print(k, v)
end
