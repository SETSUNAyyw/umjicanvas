# umjicanvas

(This Python version will no longer be maintained.)

## About

This tool will help to count everyone's activities of a user's courses in UM-JI, as well as creating a contribution plot for personalized usage.

## Requirements

- Linux based environment
- Python 3
	- pandas
	- numpy
	- argparse
	- matplotlib
	- svgpathtools
	- svgpath2mpl

## Installation

### Token

`token.txt` should be put under the root directory, which contains your access token key.

The token can be generated in your canvas settings.

**P.S. You should never give your access token key to anyone else.**

### Command Line

```bash
bash run.sh
```

## Tips

- More on [canvas API](https://canvas.instructure.com/doc/api/index.html).
- You may find some information useful in `data` folder.
- You may want to run this program at least once a day, or setup a automatic trigger to run it at customized frequency.

## Author

[Email](mailto:yangyiwen.sigo@hotmail.com)