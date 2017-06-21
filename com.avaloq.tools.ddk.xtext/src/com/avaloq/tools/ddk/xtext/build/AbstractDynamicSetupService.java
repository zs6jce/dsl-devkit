/*******************************************************************************
 * Copyright (c) 2016 Avaloq Evolution AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Avaloq Evolution AG - initial API and implementation
 *******************************************************************************/
package com.avaloq.tools.ddk.xtext.build;

import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Module;


/**
 * Support for comparing dynamic setup services.
 */
public abstract class AbstractDynamicSetupService implements IDynamicSetupService {

  /**
   * This method only exists for backward compatibility for langauges which have not yet been regenerated.
   *
   * @param overrideModule
   *          override module for the runtime module
   * @return created injector
   */
  @Deprecated
  protected Injector doSetup(final Module overrideModule) {
    // this method can be removed once all subclasses have been regenerated using DDK 4.0.0
    throw new UnsupportedOperationException();
  }

  @Override
  public Injector doSetup(final Module overrideModule, final Module... additionalModules) {
    // this implementation can be removed once all subclasses have been regenerated using DDK 4.0.0
    // until then it is required for backwards compatibility
    return doSetup(overrideModule);
  }

  /**
   * {@inheritDoc} Child languages are always > than their parent languages.
   */
  @Override
  public int compareTo(final IDynamicSetupService other) {
    if (other == this) { // NOPMD == on purpose
      return 0;
    }
    // Invalid cast will raise the ClassCastException, which is a part of 'compareTo' method's contract
    AbstractDynamicSetupService otherSetup = (AbstractDynamicSetupService) other;
    List<String> thisParentsList = getParentLanguages();
    List<String> otherParentsList = otherSetup.getParentLanguages();
    String thisRoot = thisParentsList.isEmpty() ? this.getLanguageName() : thisParentsList.get(thisParentsList.size() - 1);
    String otherRoot = otherParentsList.isEmpty() ? otherSetup.getLanguageName() : otherParentsList.get(otherParentsList.size() - 1);
    if (!thisRoot.equals(otherRoot)) {
      // If the languages have different roots, just sort them alphabetically based on root's name
      // This implies families of languages will be initialized one after another
      return thisRoot.compareTo(otherRoot);
    } else {
      // Languages belong to the same family, sorting is done:
      // 1. Based on the distance to the root grammar
      // 2. Lexicographically, if the distance is the same
      // This strategy results into BFS-like linearization which guarantees to initialize parents first
      int distanceDifference = thisParentsList.size() - otherParentsList.size();
      if (distanceDifference != 0) {
        return distanceDifference;
      } else {
        return getLanguageName().compareTo(otherSetup.getLanguageName());
      }
    }
  }

  /**
   * Returns a list of all names of parent languages (transitively).
   * The first element is the direct parent, the last one is the root grammar.
   *
   * @return the collection of names.
   */
  public abstract List<String> getParentLanguages();

  /**
   * Returns the name of this language.
   *
   * @return the name
   */
  public abstract String getLanguageName();

}
