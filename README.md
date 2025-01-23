Backend code: Java Springboot using maven
Frontend code: React and Typescript

Execution steps:
Download the folders from 
folder - "product_catalog" is for Frontend
folder - "productcatalog" is for Backend

To execute Backend:
Open cmd and navigate to the folder "productcatalog". (i.e current directory should be: productcatalog)
Type java -jar productcatalog-1.0.0.jar
The backend application will get started.
It will load 14 products.
To modify the product data (if desired): modify the file "products.csv" under productcatalog\src\main\resources\. and restart the server. (java -jar productcatalog-1.0.0.jar).

Backend application will be hosted at: http://localhost:8080
To add products:- POST: http://localhost:8080/product  (product object. Except 'id; all fields are mandatory. ID is auto-generated  by the system)
To retrieve products:- GET: http://localhost:8080/products or localhost:8080/products?pageNo=0&pageSize=5  (pageno and pagesize are optional params, if not given, default values are taken from property file).
To retrieve a product based on ID:- GET: http://localhost:8080/product/5
To retrieve products matching based on a given name:- GET: http://localhost:8080/search?query=ikea (implemented fuzzy logic and cache)

Logs will be generated in logs folder.

To execute Frontend:
Open cmd and navigate to the folder product_catalog. (i.e current directory should be: product_catalog)
type npm install (to install the dependencies)
npm start (to start the server)
Application will be hosted  at http://localhost:3000/



