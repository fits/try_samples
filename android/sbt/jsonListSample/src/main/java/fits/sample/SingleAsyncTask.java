package fits.sample;

import android.os.AsyncTask;

class SingleAsyncTask<T, U> extends AsyncTask<T, Void, U> {

	@Override
	protected U doInBackground(T... params) {
		return (params.length > 0)? doSingleTask(params[0]): null;
	}

	protected U doSingleTask(T param) {
		return null;
	}
}
