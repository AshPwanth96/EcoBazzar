import { HttpInterceptorFn, HttpErrorResponse } from "@angular/common/http";
import { inject, Inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError } from "rxjs";
import { throwError } from "rxjs";

export const AuthInterceptor:HttpInterceptorFn = (req, next)=>{
    const router = inject(Router);
    const token = localStorage.getItem('token');

    if(token){
        req = req.clone({
        setHeaders:{
            Authorization: `Bearer ${token}`

        }
    });
}

return next(req).pipe(
    catchError((err:HttpErrorResponse)=>{
        if(err.status===401||err.status===403){
            localStorage.clear();
            router.navigate(['/login']);
        }

        return throwError(()=>err);
    })
);

};