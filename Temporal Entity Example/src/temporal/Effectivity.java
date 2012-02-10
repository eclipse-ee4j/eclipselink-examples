/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package temporal;

import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * Embedded class encompassing the effectivity start and end dates as well as a
 * reference to the continuity. This class is mapped onto temporal entity
 * classes that implement {@link TemporalEntity}
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
@Embeddable
public class Effectivity {

    /**
     * Beginning Of Time (BOT) value used to indicate the current version in
     * this example since no history is maintained.
     */
    public static final long BOT = 0;

    /**
     * End Of Time (EOT) value used to represent that the edition using this
     * value has no planned termination.
     */
    public static final long EOT = Long.MAX_VALUE;

    /**
     * Start time where this edition is planned to become the current edition.
     * The current edition always has a start value of {@value #BOT}.
     */
    @Column(name = "START_TS")
    private long start = BOT;

    /**
     * The end time where this edition is planned to be replaced another
     * edition. The final edition in the sequence that has no planned terminal
     * time will have a value of {@link #EOT}
     */
    @Column(name = "END_TS")
    private long end = EOT;

    public Effectivity() {
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isCurrent() {
        return getStart() == BOT;
    }

    /**
     * @return <code>true</code> if this edition only exists in the future.
     */
    public boolean isFutureEdition() {
        return getStart() > BOT;
    }

    public String toString() {
        return "Effectivity(" + getStart() + " - " + getEnd() + ")";
    }

    public String toString(TemporalEntity<?> entity) {
        StringWriter writer = new StringWriter();

        writer.write(Helper.getShortClassName(entity));
        writer.write("(" + entity.getId() + ")");
        writer.write("Effective: ");
        writer.write(timeString(getStart()));
        writer.write(" to ");
        writer.write(timeString(getEnd()));

        return writer.toString();
    }

    private String timeString(long ts) {
        if (ts == BOT) {
            return "BOT";
        }
        if (ts == EOT) {
            return "EOT";
        }
        return Long.toString(ts);
    }
}