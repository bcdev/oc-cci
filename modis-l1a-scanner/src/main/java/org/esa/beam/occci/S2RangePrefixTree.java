package org.esa.beam.occci;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.text.ParseException;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Rectangle;
import com.spatial4j.core.shape.Shape;
import com.spatial4j.core.shape.SpatialRelation;
import org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree;

/**
 * TODO
 */
public class S2RangePrefixTree extends NumberRangePrefixTree {

  protected S2RangePrefixTree() {
    super(new int[]{1, 2, 3, 4, 5});
  }

  @Override
  public UnitNRShape toUnitShape(Object value) {
    return null;
  }

  @Override
  public Object toObject(UnitNRShape shape) {
    return null;
  }

  @Override
  protected String toString(UnitNRShape lv) {
    return null;
  }

  @Override
  protected UnitNRShape parseUnitShape(String str) throws ParseException {
    return null;
  }

  static class S2Shape implements UnitNRShape {

    @Override
    public int getLevel() {
      return 0;
    }

    @Override
    public int getValAtLevel(int level) {
      return 0;
    }

    @Override
    public UnitNRShape getShapeAtLevel(int level) {
      return null;
    }

    @Override
    public UnitNRShape roundToLevel(int targetLevel) {
      return null;
    }

    @Override
    public UnitNRShape clone() {
      return null;
    }

    @Override
    public int compareTo(UnitNRShape o) {
      return 0;
    }

    @Override
    public SpatialRelation relate(Shape other) {
      return null;
    }

    //////////////////////////////////////////////////
    // not used here
    @Override
    public Rectangle getBoundingBox() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasArea() {
      return true;
    }

    @Override
    public double getArea(SpatialContext ctx) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Point getCenter() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Shape getBuffered(double distance, SpatialContext ctx) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
      return false;
    }
  }
}
