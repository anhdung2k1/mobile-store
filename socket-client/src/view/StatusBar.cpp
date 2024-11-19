// Copyright [2024] <Anh Dung>
#include "StatusBar.h"

StatusBar::StatusBar(WINDOW *parent) {
    _parent = parent;
    _text = "";
}

StatusBar::StatusBar() {
    _parent = stdscr;
    _text = "";
}

void StatusBar::setParent(WINDOW* parent) {
    _parent = parent;
}

WINDOW* StatusBar::getParent() {
    return _parent;
}

std::string StatusBar::getText() {
    return _text;
}

void StatusBar::setText(std::string text, attr_t attributes) {
    _text = text;
    if (attributes != 0)
    {
        _attributes = attributes;
    }
}

attr_t StatusBar::getAttributes() {
    return _attributes;
}

void StatusBar::setAttributes(attr_t attributes) {
    _attributes = attributes;
}

void StatusBar::draw() {
    int output_row = _parent->_maxy - 1;
    attron(_attributes);
    mvwaddstr(_parent, output_row, 0, getText().c_str());
    attroff(_attributes);
}

void StatusBar::clear() {
    int output_row = _parent->_maxy - 1;
    move(output_row, 0);
    clrtoeol();
}