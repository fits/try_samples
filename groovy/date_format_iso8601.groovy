
def df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
df.timeZone = TimeZone.getTimeZone("GMT")

//（例） 2013-02-08T05:31:32.455Z
println df.format(new Date())

//（例） 2013-02-08T14:31:32.475+0900
println new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
