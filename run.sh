# sh ./fetch_data.sh

# my_activity_230=`python ./canvas.py VE230 -m`
# my_activity_320=`python ./canvas.py VE320 -m`
# my_activity_370=`python ./canvas.py VE370 -m`
# my_activity_471=`python ./canvas.py VE471 -m`
# my_activity_496=`python ./canvas.py VE496 -m`
# my_activity=$((my_activity_230 + my_activity_320 + my_activity_370 + my_activity_471 + my_activity_496))

# python ./canvas.py VE230 -s $my_activity

python ./canvas.py VE230 -r
python ./canvas.py VE320 -r
python ./canvas.py VE370 -r
python ./canvas.py VE471 -r
python ./canvas.py VE496 -r