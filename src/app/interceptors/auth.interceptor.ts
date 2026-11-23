
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
	// Only attach tokens to our backend API requests.
	if (!req.url.startsWith('/api/')) {
		return next(req);
	}

	// Login and registration do not require a token.
	if (
		req.url === '/api/v1/auth/login' ||
		req.url === '/api/v1/auth/register'
	) {
		return next(req);
	}

	const token = sessionStorage.getItem('authToken');

	if (!token) {
		return next(req);
	}

	const authReq = req.clone({
		setHeaders: {
			Authorization: `Bearer ${token}`
		}
	});

	return next(authReq);
};
