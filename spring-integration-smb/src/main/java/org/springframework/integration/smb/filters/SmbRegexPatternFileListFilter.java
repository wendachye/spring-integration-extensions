/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.smb.filters;

import java.util.regex.Pattern;

import org.springframework.integration.file.filters.AbstractRegexPatternFileListFilter;

import jcifs.smb.SmbFile;

/**
 * Implementation of {@link AbstractRegexPatternFileListFilter} for SMB.
 *
 * @author Markus Spann
 */
public class SmbRegexPatternFileListFilter extends AbstractRegexPatternFileListFilter<SmbFile> {

	public SmbRegexPatternFileListFilter(String pattern) {
		this(Pattern.compile(pattern));
	}

	public SmbRegexPatternFileListFilter(Pattern pattern) {
		super(pattern);
	}

	/**
	 * Gets the specified SMB file's name.
	 * @param file SMB file object
	 * @return file name
	 * @see AbstractRegexPatternFileListFilter#getFilename(java.lang.Object)
	 */
	@Override
	protected String getFilename(SmbFile file) {
		return (file != null ? file.getName() : null);
	}

}
