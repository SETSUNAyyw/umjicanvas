for i in {1..5}                                                                    
do
	var=`GET "https://umjicanvas.com/api/v1/courses/2239/users?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J&include[]=email&include[]=enrollments&per_page=50&page=$i"`
	len=`echo -n $var | wc -m`
	if [ $len -gt 10 ]; then
		echo $var >> ./VE230.txt
	fi
done

python ./canvas.py VE230
rm -f ./VE230.txt

for i in {1..5}                                                                    
do
	var=`GET "https://umjicanvas.com/api/v1/courses/2246/users?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J&include[]=email&include[]=enrollments&per_page=50&page=$i"`
	len=`echo -n $var | wc -m`
	if [ $len -gt 10 ]; then
		echo $var >> ./VE320.txt
	fi
done

python ./canvas.py VE320
rm -f ./VE320.txt

for i in {1..5}                                                                    
do
	var=`GET "https://umjicanvas.com/api/v1/courses/2249/users?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J&include[]=email&include[]=enrollments&per_page=50&page=$i"`
	len=`echo -n $var | wc -m`
	if [ $len -gt 10 ]; then
		echo $var >> ./VE370.txt
	fi
done

python ./canvas.py VE370
rm -f ./VE370.txt

for i in {1..5}                                                                    
do
	var=`GET "https://umjicanvas.com/api/v1/courses/2343/users?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J&include[]=email&include[]=enrollments&per_page=50&page=$i"`
	len=`echo -n $var | wc -m`
	if [ $len -gt 10 ]; then
		echo $var >> ./VE471.txt
	fi
done

python ./canvas.py VE471
rm -f ./VE471.txt

for i in {1..5}                                                                    
do
	var=`GET "https://umjicanvas.com/api/v1/courses/2259/users?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J&include[]=email&include[]=enrollments&per_page=50&page=$i"`
	len=`echo -n $var | wc -m`
	if [ $len -gt 10 ]; then
		echo $var >> ./VE496.txt
	fi
done

python ./canvas.py VE496
rm -f ./VE496.txt