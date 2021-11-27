export const send = async (url: string, method: string, body: any) => {
  const res = await fetch(url, {
    method,
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  })

  const resBody = await res.json()

  return {
    ok: res.ok,
    body: resBody
  }
}
