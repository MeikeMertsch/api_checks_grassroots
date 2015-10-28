# Automated checks against the API in Clojure

This project was part of my work at an earlier workplace. To be able to make this a private repo, I took a digest and cloaked much of the company specific data both through just deleting things (which will make it impossible to run the content of this repo) and through covering variables with `"HIDDEN"`. 
It is purely meant as an example of how I approached things in that job and to remind myself how I used those tools before. It's neither complete nor meant to be run.



## Currently Tested APIs:

your-tv


## Usage

To run the tests you just need to specify the environment:

    $ ./run <environment>

Whereas the `<environment>` is either `production` or `integration` for Jenkins and if you have the secret.json locally you can work with `dev-int` and `dev-prod`.


## Automated runs

The checks run currently on Jenkins.


## License

Copyright © 2015 Magine AB

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.