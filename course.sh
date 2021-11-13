for i in {1..2500}                                                                    
do
var=`GET "https://umjicanvas.com/api/v1/courses/$i?access_token=bYqU4gTmqrfeF9j6YyZ8lBzJfTlYQfvq2yW5FEgiX8ONrZEK4YBL0wpU0aPA7p6J"`
if [[ "${var:2:1}" == "i" ]]; then
	printf "\n" >> ~/courses.txt
	echo $var >> ~/courses.txt
fi
done
