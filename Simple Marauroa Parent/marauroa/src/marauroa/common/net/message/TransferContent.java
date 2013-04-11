/***************************************************************************
 *                   (C) Copyright 2003-2012 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package marauroa.common.net.message;

import java.io.IOException;

import marauroa.common.Utility;
import marauroa.common.crypto.Hash;
import marauroa.common.net.NetConst;

/**
 * A helper class to transfer content from server to client.
 *
 * @author miguel
 *
 */
public class TransferContent {

	/**
	 * Name of the content to transfer.
	 * Usually it is a file name.
	 */
	public String name;

	/**
	 * When this content was created or any other way of stamping the content for
	 * version control.
	 */
	public int timestamp;

	/** The content itself. */
	public byte[] data;

	/** If the client can cache this content,  this would be true. */
	public boolean cacheable;

	/** If the client approved this content to be transfered it will be true. */
	public boolean ack;

	/** a hash of the data */
	private byte[] hash;

	/**
	 * Constructor
	 *
	 */
	public TransferContent() {
		ack = false;
		cacheable = false;
		data = null;
		name = null;
		timestamp = 0;
	}

	/**
	 * gets the hash
	 *
	 * @return hash
	 */
	public byte[] getHash() {
		if (hash == null) {
			hash = Hash.hash(data);
		}
		return hash;
	}

	/**
	 * gets the transmitted hash, may be <code>null</code>.
	 *
	 * @return transmitted hash or <code>null</code>
	 */
	public byte[] getTransmittedHash() {
		return hash;
	}

	@Override
	public String toString() {
		StringBuffer sstr = new StringBuffer();

		sstr.append("TransferContent: [name=\"");
		sstr.append(name);
		sstr.append("\" timestamp=\"");
		sstr.append(timestamp);
		sstr.append("\"]");

		return sstr.toString();
	}

	/**
	 * Constructor
	 * @param name name of the content
	 * @param timestamp version control timestamp.
	 * @param data data of the content.
	 */
	public TransferContent(String name, int timestamp, byte[] data) {
		this.name = name;
		this.timestamp = timestamp;
		this.data = Utility.copy(data);
		cacheable = true;
		ack = false;
	}

	/**
	 * Write content as a request to client to approve it
	 * @param out
	 * @throws IOException
	 */
	public void writeREQ(marauroa.common.net.OutputSerializer out) throws IOException {
		out.write(name);
		out.write(timestamp);
		if (out.getProtocolVersion() >= NetConst.FIRST_VERSION_WITH_CONTENT_HASH) {
			out.write(getHash());
		}
		out.write((byte) (cacheable ? 1 : 0));
	}

	/**
	 * Reads the content transfer request.
	 * @param in
	 * @throws IOException
	 */
	public void readREQ(marauroa.common.net.InputSerializer in) throws IOException {
		name = in.readString();
		timestamp = in.readInt();
		if (in.getProtocolVersion() >= NetConst.FIRST_VERSION_WITH_CONTENT_HASH) {
			hash = in.readByteArray();
		}
		cacheable = (in.readByte() == 1);
	}

	/**
	 * Write a content acceptance to server.
	 * @param out
	 * @throws IOException
	 */
	public void writeACK(marauroa.common.net.OutputSerializer out) throws IOException {
		out.write(name);
		out.write((byte) (ack ? 1 : 0));
	}

	/**
	 * Reads the content acceptance from client
	 * @param in
	 * @throws IOException
	 */
	public void readACK(marauroa.common.net.InputSerializer in) throws IOException {
		name = in.readString();
		ack = (in.readByte() == 1);
	}

	/**
	 * Write the content data to client
	 * @param out
	 * @throws IOException
	 */
	public void writeFULL(marauroa.common.net.OutputSerializer out) throws IOException {
		out.write(name);
		out.write(data);
		out.write(timestamp);
		if (out.getProtocolVersion() >= NetConst.FIRST_VERSION_WITH_CONTENT_HASH) {
			out.write(getHash());
		}
		out.write((byte) (cacheable ? 1 : 0));
	}

	/**
	 * Read the content data from server.
	 * @param in
	 * @throws IOException
	 */
	public void readFULL(marauroa.common.net.InputSerializer in) throws IOException {
		name = in.readString();
		data = in.readByteArray();
		timestamp = in.readInt();
		if (in.getProtocolVersion() >= NetConst.FIRST_VERSION_WITH_CONTENT_HASH) {
			hash = in.readByteArray();
		}
		cacheable = (in.readByte() == 1);
	}
}
