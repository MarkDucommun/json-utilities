import csv

from fitter import Fitter
# import numpy as np
# import matplotlib.mlab as mlab
# import matplotlib.pyplot as plt


def main():
    with open('/Users/xpdesktop/workspace/json-schema-parser/claim-size-list.csv') as f:
        reader = csv.reader(f, delimiter=',', quotechar='|')

        your_list = list(reader)
        int_list = [int(i) for i in your_list[0] if i != ""]

        f = Fitter(int_list)
        f.fit()
        # may take some time since by default, all distributions are tried
        # but you call manually provide a smaller set of distributions
        f.summary()

        # num_bins = 1000
        # fig, ax = plt.subplots()
        #
        # # the histogram of the data
        # n, bins, patches = ax.hist(int_list, num_bins)
        #
        # print n
        # print bins
        #
        # # add a 'best fit' line
        # # y = mlab.normpdf(bins, mu, sigma)
        # # ax.plot(bins, y, '--')
        #
        # ax.set_xlabel('Size in Bytes')
        # ax.set_ylabel('Count?')
        # ax.set_title('Claim Size Distribution')
        #
        # # Tweak spacing to prevent clipping of ylabel
        # fig.tight_layout()
        # plt.show()


if __name__ == '__main__':
    main()