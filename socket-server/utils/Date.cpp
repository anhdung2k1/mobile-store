#include <iostream>

using namespace std;

class Date {
    private:
        int day, month, year;
    public:
        Date(int day, int month, int year) : day(day), month(month), year(year) {}
        int getDay() { return day; }
        int getMonth() { return month; }
        int getYear() { return year; }
        void setDay(int day) { this->day = day; }
        void setMonth(int month) { this->month = month; }
        void setYear(int year) { this->year = year; }
};