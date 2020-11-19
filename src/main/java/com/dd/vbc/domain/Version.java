package com.dd.vbc.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Version implements DomainObject {

    private Integer major;
    private Integer minor;

    public Version(Integer major, Integer minor) {
        this.major = major;
        this.minor = minor;
    }

    public Integer getMajor() {
        return major;
    }
    public Integer getMinor() {
        return minor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        return new EqualsBuilder()
                .append(major, version.major)
                .append(minor, version.minor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(major)
                .append(minor)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Version{" +
                "major=" + major +
                ", minor=" + minor +
                '}';
    }
}
