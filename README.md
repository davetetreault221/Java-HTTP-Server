# Foobar

A small program to demonstrate HTTP Server functionality provided by the Java Class 

## Installation

No installation, simply run in the terminal 

## Usage

```python

//TO RUN IN TERMINAL
java MainServer -v -p 8080 -d ./httpServer/src/Disk

//COMMAND FOR POST
curl -X POST --data "This is information for the file" localhost:8080/check

//COMMAND FOR GET
curl -X GET localhost:8080/foo

//Main path
Main path should be set to your working directory in order to function
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
