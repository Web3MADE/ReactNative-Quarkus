import storage from "../config/Storage";

export async function getJwtToken() {
  return new Promise((resolve, reject) => {
    storage
      .load({
        key: "token",

        // autoSync (default: true) means if data is not found or has expired,
        // then invoke the corresponding sync method
        autoSync: true,

        // syncInBackground (default: true) means if data expired,
        // return the outdated data first while invoking the sync method.
        // If syncInBackground is set to false, and there is expired data,
        // it will wait for the new data and return only after the sync completed.
        // (This, of course, is slower)
        syncInBackground: true,

        // you can pass extra params to the sync method
        // see sync example below
        syncParams: {
          extraFetchOptions: {
            // blahblah
          },
          someFlag: true,
        },
      })
      .then((res) => {
        // found data go to then()
        resolve({ res });
      })
      .catch((err) => {
        // any exception including data not found
        // goes to catch()
        console.warn(err.message);
        reject({ err });
      });
  });
}
