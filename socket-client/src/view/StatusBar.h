// Copyright [2024] <Anh Dung>
#pragma once
#include <ncurses.h>
#include <string>

class StatusBar
{
    WINDOW *_parent;
    std::string _text;
    attr_t _attributes;

 public:
    StatusBar();
    StatusBar(WINDOW *parent);
    void setParent(WINDOW* parent);
    WINDOW* getParent();
    std::string getText();
    void setText(std::string text, attr_t attributes);
    attr_t getAttributes();
    void setAttributes(attr_t attributes);
    void draw();
    void clear();
};