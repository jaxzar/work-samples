using System;
using System.IO;
using System.Drawing;

/// <summary>
/// Green Lightning Software Ini Tools (GLSIT) components. This class
/// contains built-in tools to read and write values to and from .ini
/// settings files, and does all the File I/O for you. Simply import
/// the namespace and declare either an IniWriter or an IniReader, and
/// let the class do all the work for you. Makes ini files appear to
/// be random access, as you can store values in different sections and
/// retrieve them in any order you wish.
/// </summary>
namespace GLSIniTools
{
	/// <summary>
	/// The IniWriter class writes values to a .ini file.
	/// </summary>
	public class IniWriter: IniTool
	{
		private StreamWriter _out;

		/// <summary>
		/// Sets up an IniWriter with [NumValues] slots for values to
		/// write to a .ini file.
		/// </summary>
		/// <param name="FileName">The name of the file to write to.</param>
		/// <param name="NumSections">The number of section slots.</param>
		public IniWriter(string FileName, int NumSections): base (FileName, NumSections)
		{ }

		/// <summary>
		/// Sets up a new section to add values to. This must be called [NumSections]
		/// times before anything is written to the file.
		/// </summary>
		/// <param name="Name">The name of the section.</param>
		/// <param name="NumValues">The number of values this section will contain.</param>
		public void AddNewSection(string Name, int NumValues)
		{
			if (currSect < NumSections)
			{
				sections[currSect] = Name;
				MyStream[currSect++] = new Entry[NumValues];
			}
		}

		/// <summary>
		/// This method actually performs all the write operations. Since this
		/// class makes it appear to the client that the .ini file is random
		/// access, all the data is still in the arrays waiting to be written.
		/// This writes all the data to the file at once, then flushes and
		/// closes the stream.
		/// </summary>
		public override void CloseFile()
		{
			_out = new StreamWriter(myFile);
			_out.WriteLine("[GLSIT={0}]", NumSections);

			for (int i = 0; i < NumSections; i++)
			{
				_out.WriteLine();
				_out.WriteLine("[{0}={1}]",sections[i],MyStream[i].Length);
				foreach (Entry e in MyStream[i])
					_out.WriteLine("{0}={1}",e.EntryName,e.EntryValue);
			}

			_out.Flush();
			_out.Close();
		}

		/// <summary>
		/// Writes a boolean value in a certain section of the .ini file.
		/// </summary>
		/// <param name="_value">The value to write.</param>
		/// <param name="_name">The name of the value.</param>
		/// <param name="sec">The name of the section to write the value in.</param>
		public void WriteBool(bool _value, string _name, string sec)
		{
			int s = GetSectNum(sec);
			MyStream[s][currStream[s]].EntryName = _name;
			MyStream[s][currStream[s]++].EntryValue = (_value ? "yes" : "no");
		}

		/// <summary>
		/// Writes a string value in a certain section of the .ini file.
		/// </summary>
		/// <param name="_value">The value to write.</param>
		/// <param name="_name">The name of the value.</param>
		/// <param name="sec">The name of the section to write the value in.</param>
		public void WriteString(string _value, string _name, string sec)
		{
			int s = GetSectNum(sec);
			MyStream[s][currStream[s]].EntryName = _name;
			MyStream[s][currStream[s]++].EntryValue = _value;
		}

		/// <summary>
		/// Writes an RGB hex-color value in a certain section of the .ini file.
		/// </summary>
		/// <param name="_value">The Color to write.</param>
		/// <param name="_name">The name of the color value.</param>
		/// <param name="sec">The name of the section to write the value in.</param>
		public void WriteColor(Color _value, string _name, string sec)
		{
			int s = GetSectNum(sec);
			MyStream[s][currStream[s]].EntryName = _name;
			MyStream[s][currStream[s]++].EntryValue = ColorTranslator.ToWin32(_value).ToString();			
		}

		/// <summary>
		/// Writes an RGB hex-color value in a certain section of the .ini file.
		/// </summary>
		/// <param name="_red">The red component of the color (0-255).</param>
		/// <param name="_green">The green component of the color (0-255).</param>
		/// <param name="_blue">The blue component of the color (0-255).</param>
		/// <param name="_name">The name of the color value.</param>
		/// <param name="sec">The name of the section to write the value in.</param>
		public void WriteColor(int _red, int _green, int _blue, string _name, string sec)
		{
			WriteColor(Color.FromArgb(_red, _green, _blue), _name, sec);
		}

		/// <summary>
		/// Writes an integer value in a certain section of the .ini file.
		/// </summary>
		/// <param name="_value">The value to write.</param>
		/// <param name="_name">The name of the value.</param>
		/// <param name="sec">The name of the section to write the value in.</param>
		public void WriteInt(int _value, string _name, string sec)
		{
			int s = GetSectNum(sec);
			MyStream[s][currStream[s]].EntryName = _name;
			MyStream[s][currStream[s]++].EntryValue = _value.ToString();
		}
	}

	/// <summary>
	/// The IniReader class reads values from a .ini file.
	/// </summary>
	public class IniReader: IniTool
	{
		private StreamReader _in;
		public IniReader(string FileName) : base (FileName, 0)
		{
			if (IsValidFile())
				_in.Close();
			else
				System.Windows.Forms.MessageBox.Show(
					"The file " + FileName + " either does not exist or is not a " +
					"valid INI file for use with the GL-Soft INI Tools package.",
					"Error Reading File"
					);
		}

		/// <summary>
		/// Unnecessary. It is only here because of the abstract method in the
		/// base class, IniTool. The client can continue to read information
		/// from the stream at any point once this object has been instantiated.
		/// This implementation of CloseFile() does absolutely nothing, therefore
		/// calling it is pointless.
		/// </summary>
		public override void CloseFile() { }

		/// <summary>
		/// Does all the initial reading and parsing of the .ini file. Once
		/// this function has been run, all the data has been read in and
		/// is in the MyStream array, ready for access. The ReadInt(),
		/// ReadBool(), and ReadColor() functions are merely random-access
		/// utility methods for the client's convenience.
		/// </summary>
		/// <returns>True if the file is valid; false if it is corrupt or
		/// unable to be read properly due to incorrect formatting.</returns>
		private bool IsValidFile()
		{
			try 
			{
				_in = new StreamReader(myFile);
				string d = "";
				string tmp = _in.ReadLine();
				tmp = StripBrackets(tmp);
				string tmp2 = Parse(tmp, true);
				tmp = Parse(tmp, false);
				NumSections = int.Parse(tmp);
				MyStream = new Entry[NumSections][];
				sections = new string[NumSections];

				if (tmp2.Equals("GLSIT"))
				{
					tmp = _in.ReadLine(); // dummy read to skip a line
					tmp = "";
					tmp2 = "";
					for (int i = 0; i < NumSections; i++)
					{
						// Now ready to read the file in by sections
						d = _in.ReadLine();
						d = StripBrackets(d);
						tmp = Parse(d, true); // Get section name
						sections[i] = tmp;
						
						// Get number of values in this section
						tmp2 = Parse(d, false);
						int numValues = int.Parse(tmp2);

						// Set up new array for this section
						MyStream[i] = new Entry[numValues];						

						// Read it in
						for (int j = 0; j < numValues; j++)
						{
							d = _in.ReadLine();
							MyStream[i][j].EntryName = Parse(d, true);
							MyStream[i][j].EntryValue = Parse(d, false);
						}

						if (i < NumSections - 1)	// if not the last section,
							d = _in.ReadLine();		// read the empty line at the end
					}
					return true;
				}
				else
					throw new Exception("File format not recognized.");
			}
			catch (Exception e)
			{
				System.Windows.Forms.MessageBox.Show(
					"The file " + myFile + " appears to be invalid and was " +
					"unable to be read. Exception: " + e.ToString() +
					" occurred while trying to read file.","Error Reading File"
					);
			}
			return false;
		}

		/// <summary>
		/// Finds and reads a boolean value from the .ini file.
		/// </summary>
		/// <param name="Name">The name of the value to read.</param>
		/// <param name="Section">The name of the section containing the value.</param>
		/// <returns>The boolean value read from the .ini file.</returns>
		/// <exception cref="SectionNotFoundException">Throws this exception
		/// if the given section could not be found.</exception>
		/// <exception cref="ValueNotFoundException">Throws this exception
		/// if a value with the given name could not be found.</exception>
		/// <exception cref="InvalidCastException">Throws this exception
		/// if a value with the given name exists, but was not written to
		/// the .ini file as a boolean or string value. Boolean values may
		/// be stored as either booleans or strings and read back in as such,
		/// since they are stored in the .ini file as string values of "yes"
		/// and "no".</exception>
		public bool ReadBool(string _name, string sec)
		{
			int s = GetSectNum(sec);
			if (s < 0)
				throw new SectionNotFoundException(sec);
			else
			{
				for (int i = 0; i < NumSections; i++)
					foreach (Entry e in MyStream[i])
						if (e.EntryName.Equals(_name))
							return (e.EntryValue.ToLower().Equals("yes"));

				throw new ValueNotFoundException(_name);
			}
		}

		/// <summary>
		/// Finds and reads a color value from the .ini file.
		/// </summary>
		/// <param name="_name">The name of the value to read.</param>
		/// <param name="sec">The name of the section containing the value.</param>
		/// <returns>The color value read from the .ini file.</returns>
		/// <exception cref="SectionNotFoundException">Throws this exception
		/// if the given section could not be found.</exception>
		/// <exception cref="ValueNotFoundException">Throws this exception
		/// if a value with the given name could not be found.</exception>
		/// <exception cref="InvalidCastException">Throws this exception
		/// if a value with the given name exists, but was written to
		/// the .ini file as a string or boolean value. However, colors may
		/// be stored as integers and read back in as colors, since they are
		/// stored in the .ini file as integers.</exception>
		public Color ReadColor(string _name, string sec)
		{
			int s = GetSectNum(sec);
			if (s < 0)
				throw new SectionNotFoundException(sec);
			else
			{
				for (int i = 0; i < NumSections; i++)
					foreach (Entry e in MyStream[i])
						if (e.EntryName.Equals(_name))
							return (ColorTranslator.FromWin32(Int32.Parse(e.EntryValue)));

				throw new ValueNotFoundException(_name);
			}
		}

		/// <summary>
		/// Finds and reads an integer value from the .ini file.
		/// </summary>
		/// <param name="_name">The name of the value to read.</param>
		/// <param name="sec">The name of the section containing the value.</param>
		/// <returns>The integer value read from the .ini file.</returns>
		/// <exception cref="SectionNotFoundException">Throws this exception
		/// if the given section could not be found.</exception>
		/// <exception cref="ValueNotFoundException">Throws this exception
		/// if a value with the given name could not be found.</exception>
		/// <exception cref="InvalidCastException">Throws this exception
		/// if a value with the given name exists, but was not written to
		/// the .ini file as an integer value.</exception>
		public int ReadInt(string _name, string sec)
		{
			int s = GetSectNum(sec);
			if (s < 0)
				throw new SectionNotFoundException(sec);
			else
			{
				for (int i = 0; i < NumSections; i++)
					foreach (Entry e in MyStream[i])
						if (e.EntryName.Equals(_name))
							return (int.Parse(e.EntryValue));

				throw new ValueNotFoundException(_name);
			}
		}

		/// <summary>
		/// Finds and reads a string value from the .ini file.
		/// </summary>
		/// <param name="_name">The name of the value to read.</param>
		/// <param name="sec">The name of the section containing the value.</param>
		/// <returns>The string value read from the .ini file.</returns>
		/// <exception cref="SectionNotFoundException">Throws this exception
		/// if the given section cannot be found.</exception>
		/// <exception cref="ValueNotFoundException">Throws this exception
		/// if a value with the given name cannot be found.</exception>
		/// <remarks>Since all values are initially read in as strings, this
		/// function will not throw an InvalidCastException if an int, color,
		/// or boolean value is mistakenly read in as a string. For this reason,
		/// make sure you call the correct function for getting your values
		/// back from the .ini file unless, for some reason, you wish to read
		/// the value in as a string and parse or process it yourself.</remarks>
		public string ReadString(string _name, string sec)
		{
			int s = GetSectNum(sec);
			if (s < 0)
				throw new SectionNotFoundException(sec);
			else
			{
				for (int i = 0; i < NumSections; i++)
					foreach (Entry e in MyStream[i])
						if (e.EntryName.Equals(_name))
							return e.EntryValue;

				throw new ValueNotFoundException(_name);
			}
		}
	}

	#region Extra Classes & Structs
	/// <summary>
	/// Base class for IniWriter and IniReader. Contains members and functions
	/// needed in both subclasses.
	/// </summary>
	public abstract class IniTool
	{
		protected Entry[][] MyStream;
		protected string[] sections;
		protected string myFile;
		protected int NumSections;
		protected int[] currStream;
		protected int currSect;

		public abstract void CloseFile();
		
		public IniTool(string FileName, int NumSections)
		{
			this.NumSections = NumSections;
			myFile = FileName;
			sections = new string[NumSections];
			MyStream = new Entry[NumSections][];
			currStream = new int[NumSections];
			for (int i = 0; i < NumSections; i++)
				currStream[i] = 0;
			currSect = 0;
		}

		/// <summary>
		/// Finds the index of a given section.
		/// </summary>
		/// <param name="sect">The name of the section to search for.</param>
		/// <returns>The index of the section, or -1 if the section could
		/// not be found.</returns>
		protected int GetSectNum(string sect)
		{
			if (sections != null)
				for (int i = 0; i < NumSections; i++)
					if (sections[i].Equals(sect))
						return i;
				
			return -1;
		}

        /// <summary>
		/// Gets either the name or value of an entry of the form [name]=[value],
		/// which all values written by this library are.
		/// </summary>
		/// <param name="n">The string to parse.</param>
		/// <param name="name">Whether to return the name or the value.</param>
		/// <returns>The name if true was passed, or the value as a
		/// string if false was passed.</returns> 
		protected string Parse(string n, bool name)
		{
			if (n.IndexOf("=") < 0)
				return "";
			else
			{
				if (!name)
					return n.Substring(n.IndexOf("=") + 1); // get value
				else
					return n.Substring(0, n.IndexOf("=")); // get value name
			}
		}

		/// <summary>
		/// Strips the brackets from a section name (turns "[sect]" into "sect")
		/// but does nothing if either or both brackets are missing, or are not
		/// at both ends of the string with the correct orientation.
		/// </summary>
		/// <param name="n">The string to strip.</param>
		/// <returns>The stripped string.</returns> 
		protected string StripBrackets(string n)
		{
			if (n[0] == '[' && n[n.Length - 1] == ']')
				return n.Substring(1, n.Length - 2);
			else
				return n;
		}
	}

	public struct Entry
	{
		public string EntryName;
		public string EntryValue;
		public Entry(string n, string v)
		{
			EntryName = n;
			EntryValue = v;
		}
	}

	public class SectionNotFoundException : Exception
	{
		public SectionNotFoundException(string s_name):
			base ("Invalid section name: " + s_name + " not found.")
		{ }
	}

	public class ValueNotFoundException : Exception
	{
		public ValueNotFoundException(string v_name):
			base ("Invalid value name: " + v_name + " not found.")
		{ }
	}
	#endregion
}
