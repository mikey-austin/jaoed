#!/usr/bin/python3

import sys
import binascii

#Global header for pcap 2.4
pcap_global_header = ('D4 C3 B2 A1'
                      '02 00'         #File format major revision (i.e. pcap <2>.4)
                      '04 00'         #File format minor revision (i.e. pcap 2.<4>)
                      '00 00 00 00'
                      '00 00 00 00'
                      'FF FF 00 00'
                      '01 00 00 00')

#pcap packet header that must preface every packet
pcap_packet_header = ('AA 77 9F 47'
                      '90 A2 04 00'
                      'XX XX XX XX'   #Frame Size (little endian)
                      'YY YY YY YY')  #Frame Size (little endian)

eth_header = ('DE AD BE EF 00 01'     #Dst Mac
              'DE AD BE EF 00 02'     #Src Mac
              '88 A2')                #Protocol (0x0800 = IP)

aoe_header = ('10'                    #Version 1 & 4 bits for flags
              '00'                    #Error byte
              'XX XX'                 #Major device number
              'YY'                    #Minor device number
              'CC'                    #Command
              'TT TT TT TT'           #Tag
              'AA AA AA AA')          #Arg

def getByteLength(str1):
    return int(len('' . join(str1.split())) / 2)

def writeByteStringToFile(bytestring, filename):
    bytelist = bytestring.split()
    bytes = binascii.a2b_hex(''.join(bytelist))
    bitout = open(filename, 'wb')
    bitout.write(bytes)

def generatePCAP(pcapfile, major, minor, command, tag, arg):

    aoe_len = getByteLength(aoe_header)
    aoe = aoe_header.replace('XX XX',"%04x" % major)
    aoe = aoe.replace('YY',"%02x" % minor)
    aoe = aoe.replace('CC',"%02x" % command)
    aoe = aoe.replace('TT TT TT TT',"%08x" % tag)
    aoe = aoe.replace('AA AA AA AA',"%08x" % arg)

    pcap_len = aoe_len + getByteLength(eth_header)
    hex_str = "%08x" % pcap_len
    reverse_hex_str = hex_str[6:] + hex_str[4:6] + hex_str[2:4] + hex_str[:2]
    pcaph = pcap_packet_header.replace('XX XX XX XX', reverse_hex_str)
    pcaph = pcaph.replace('YY YY YY YY', reverse_hex_str)

    bytestring = pcap_global_header + pcaph + eth_header + aoe
    writeByteStringToFile(bytestring, pcapfile)

#Splits the string into a list of tokens every n characters
def splitN(str1, n):
    return [str1[start:start+n] for start in range(0, len(str1), n)]

"""------------------------------------------"""
""" End of functions, execution starts here: """
"""------------------------------------------"""

if len(sys.argv) < 2:
    print('usage: pcapgen.py output_file')
    exit(0)

generatePCAP(sys.argv[1], 100, 3, 1, 0, 0)
