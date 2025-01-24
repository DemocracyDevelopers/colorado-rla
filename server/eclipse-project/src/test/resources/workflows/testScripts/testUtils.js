export function authSanityCheck(login, stage, statusExpected) {
  client.test("auth sanity check", () => {
  const authStatus = JSON.parse(response.body)['stage'];
  client.log("Auth status for "+"login "+login+" is "+authStatus);
  client.assert(authStatus === statusExpected, 'Stage ' + stage + 'auth failed.');
})
}
