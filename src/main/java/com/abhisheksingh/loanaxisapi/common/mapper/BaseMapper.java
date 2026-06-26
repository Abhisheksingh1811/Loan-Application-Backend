package com.abhisheksingh.loanaxisapi.common.mapper;

public interface BaseMapper<Source, Target> {
    Target map(Source source);
}
