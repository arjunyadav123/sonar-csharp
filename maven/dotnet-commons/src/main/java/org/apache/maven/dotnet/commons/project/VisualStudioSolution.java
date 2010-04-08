/**
 * Maven and Sonar plugin for .Net
 * Copyright (C) 2010 Jose Chillan and Alexandre Victoor
 * mailto: jose.chillan@codehaus.org or alexandre.victoor@codehaus.org
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

/*
 * Created on Apr 16, 2009
 */
package org.apache.maven.dotnet.commons.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A visual studio solution model.
 * 
 * @author Jose CHILLAN Apr 16, 2009
 */
public class VisualStudioSolution
{
  private File                      solutionFile;
  private File                      solutionDir;
  private String                    name;
  private List<VisualStudioProject> projects;

  public VisualStudioSolution(File solutionFile, List<VisualStudioProject> projects)
  {
    this.solutionFile = solutionFile;
    this.solutionDir = solutionFile.getParentFile();
    this.projects = projects;
  }

  /**
   * Gets the project a file belongs to.
   * 
   * @param file
   * @return the project contains the file, or <code>null</code> if none is matching
   */
  public VisualStudioProject getProject(File file)
  {
    for (VisualStudioProject project : projects)
    {
      if (project.contains(file))
      {
        return project;
      }
    }
    return null;
  }

  /**
   * Gets the project whose base directory contains the file.
   * 
   * @param file the file to look for
   * @return the associated project, or <code>null</code> if none is matching
   */
  public VisualStudioProject getProjectByLocation(File file)
  {
    String canonicalPath;
    try
    {
      canonicalPath = file.getCanonicalPath();
      for (VisualStudioProject project : projects)
      {
        File directory = project.getDirectory();
        String projectFolderPath = directory.getPath();
        if (canonicalPath.startsWith(projectFolderPath))
        {
          if (project.isParentDirectoryOf(file))
          {
            return project;
          }
        }
      }
    }
    catch (IOException e)
    {
      // Nothing
    }

    return null;
  }

  /**
   * Returns the solutionFile.
   * 
   * @return The solutionFile to return.
   */
  public File getSolutionFile()
  {
    return this.solutionFile;
  }

  /**
   * Returns the solutionDir.
   * 
   * @return The solutionDir to return.
   */
  public File getSolutionDir()
  {
    return this.solutionDir;
  }

  /**
   * Gets a project by its assembly name.
   * 
   * @param assemblyName the name of the assembly
   * @return the project, or <code>null</code> if not found
   */
  public VisualStudioProject getProject(String assemblyName)
  {
    for (VisualStudioProject project : projects)
    {
      if (assemblyName.equalsIgnoreCase(project.getAssemblyName()))
      {
        return project;
      }
    }
    return null;
  }

  /**
   * Returns the projects.
   * 
   * @return The projects to return.
   */
  public List<VisualStudioProject> getProjects()
  {
    return this.projects;
  }

  /**
   * Returns the test projects.
   * 
   * @return The projects to return.
   */
  public List<VisualStudioProject> getTestProjects()
  {
    List<VisualStudioProject> result = new ArrayList<VisualStudioProject>();
    for (VisualStudioProject visualStudioProject : projects)
    {
      if (visualStudioProject.isTest())
      {
        result.add(visualStudioProject);
      }
    }
    return result;
  }

  @Override
  public String toString()
  {
    return "Solution(path=" + solutionFile + ")";
  }

  /**
   * Returns the name.
   * 
   * @return The name to return.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Sets the name.
   * 
   * @param name The name to set.
   */
  public void setName(String name)
  {
    this.name = name;
  }

}